import { UserRoles } from "src/app/shared/constants/user-roles";

export interface User {
  id: string;
  email: string;
  roles: [UserRoles];
}
