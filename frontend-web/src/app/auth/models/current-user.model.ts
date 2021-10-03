import { UserRoles } from '../../shared/constants/user-roles';
import firebase from 'firebase/app';

export class CurrentUser {
  constructor(
    public token: string,
    public role: UserRoles,
    public email: string
  ) {}

  static async build(firebaseUser: firebase.User) {
    const decodedToken = await firebaseUser.getIdTokenResult(true);
    const token = await firebaseUser.getIdToken();
    const role = decodedToken.claims[UserRoles.ROLE_ASSOCIATE]
      ? UserRoles.ROLE_ASSOCIATE
      : UserRoles.ROLE_MANAGER;
    const email = firebaseUser.email!;
    return new CurrentUser(token, role, email);
  }
}
