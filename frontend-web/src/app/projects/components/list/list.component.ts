import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Store } from '@ngrx/store';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { AppState } from 'src/app/app.state';
import { AuthSelectors } from 'src/app/auth/state/auth.selectors';
import { UserRoles } from 'src/app/shared/constants/user-roles';
import { ProjectDisplay } from '../../models/project-display.model';
import { ProjectsSelectors } from '../../state/projects.selectors';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.scss'],
})
export class ListComponent implements AfterViewInit, OnInit, OnDestroy {
  displayedColumns = ['title', 'TODO', 'IN_PROGRESS', 'DONE', 'id'];
  dataSource = new MatTableDataSource<ProjectDisplay>();
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  subscription!: Subscription;
  isAssociate$!: Observable<boolean>;

  constructor(private store: Store<AppState>) {}

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  ngOnInit() {
    this.subscription = this.store
      .select(ProjectsSelectors.display)
      .subscribe((projects) => (this.dataSource.data = projects));

    this.isAssociate$ = this.store
      .select(AuthSelectors.user)
      .pipe(map((user) => user?.role === UserRoles.ROLE_ASSOCIATE));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
