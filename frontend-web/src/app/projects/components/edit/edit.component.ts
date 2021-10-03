import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Observable, Subscription } from 'rxjs';
import { User } from 'src/app/users/models/user.model';
import { map, startWith, take, tap } from 'rxjs/operators';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Store } from '@ngrx/store';
import { AppState } from 'src/app/app.state';
import { UsersSelectors } from 'src/app/users/state/users.selectors';
import { ProjectsSelectors } from '../../state/projects.selectors';
import { ProjectsActions } from '../../state/projects.actions';

@Component({
  selector: 'app-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.scss'],
})
export class EditComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  separatorKeys = [ENTER, COMMA];
  filteredUsers$!: Observable<User[]>;
  selectedUsers: User[] = [];
  associates: User[] = [];
  @ViewChild('associatesInput') associatesInput!: ElementRef<HTMLInputElement>;
  usersSubscription!: Subscription;
  editMode!: boolean;
  editId!: string | null;
  loading$!: Observable<boolean>;

  constructor(private store: Store<AppState>, private route: ActivatedRoute) {}

  ngOnInit() {
    this.usersSubscription = this.store
      .select(UsersSelectors.selectAssociates)
      .subscribe((associates) => (this.associates = associates));

    this.loading$ = this.store.select(ProjectsSelectors.loading);

    // take(1) should automatically unsubscribe 🤞

    this.route.params.pipe(take(1)).subscribe((params) => {
      if (params.id) {
        this.editMode = true;
        this.editId = params.id;
        this.store
          .select(ProjectsSelectors.selectOne(params.id))
          .pipe(take(1))
          .subscribe((project) => {
            this.form = new FormGroup({
              title: new FormControl(project!.title),
              associates: new FormControl(),
            });
            this.selectedUsers = this.associates.filter((user) =>
              (project!.associatesIds || []).includes(user.id)
            );
          });
      } else {
        this.editMode = false;
        this.editId = null;
        this.form = new FormGroup({
          title: new FormControl(),
          associates: new FormControl(),
        });
      }
    });

    this.filteredUsers$ = this.form.get('associates')?.valueChanges.pipe(
      startWith(null),
      map((user) => this.filter(user))
    )!;
  }

  select(event: MatAutocompleteSelectedEvent) {
    this.selectedUsers.push(
      this.associates.find((user) => user.email === event.option.viewValue)!
    );
    this.associatesInput.nativeElement.value = '';
    this.form.patchValue({ associates: '' });
  }

  remove(user: User) {
    const index = this.selectedUsers.indexOf(user);
    if (index >= 0) {
      this.selectedUsers.splice(index, 1);
    }
    this.form.patchValue({ associates: '' });
  }

  private filter(value: User | string | null | undefined) {
    const usersNotSelected = this.associates.filter(
      (user) => !this.selectedUsers.includes(user)
    );
    if (typeof value === 'string') {
      return usersNotSelected.filter((user) => user.email.includes(value));
    }
    return usersNotSelected;
  }

  onSubmit() {
    const associatesIds = this.selectedUsers.map((user) => user.id);
    const title = this.form.value.title;
    const project = { title, associatesIds };
    if (this.editMode) {
      this.store.dispatch(
        ProjectsActions.startUpdate({
          project: { ...project, id: this.editId! },
        })
      );
    } else {
      this.store.dispatch(ProjectsActions.startCreate({ project }));
    }
  }

  onDelete() {
    this.store.dispatch(ProjectsActions.startDelete({ id: this.editId! }));
  }

  ngOnDestroy() {
    this.usersSubscription.unsubscribe();
  }
}
