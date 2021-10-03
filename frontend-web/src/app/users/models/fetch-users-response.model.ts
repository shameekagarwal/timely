import { UserRoles } from 'src/app/shared/constants/user-roles';

export interface FetchUsersResponse {
  displayName: string;
  email: string;
  roles: [UserRoles];
  uid: string;
}
