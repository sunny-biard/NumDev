import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';
import { expect } from '@jest/globals';

import { MeComponent } from './me.component';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { UserService } from 'src/app/services/user.service';
import { of } from 'rxjs';
import { User } from 'src/app/interfaces/user.interface';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let mockRouter: jest.Mocked<Router>;
  let mockSnackBar: jest.Mocked<MatSnackBar>;
  let mockUserService: jest.Mocked<UserService>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn()
  }

  const mockUser: User = {
    id: 1,
    email: 'test@test.fr',
    lastName: 'Dupont',
    firstName: 'Jean',
    admin: false,
    password: 'azerty',
    createdAt: new Date(),
  };

  beforeEach(async () => {
    mockUserService = {
      getById: jest.fn().mockReturnValue(of(mockUser)),
      delete: jest.fn().mockReturnValue(of({}))
    } as unknown as jest.Mocked<UserService>;

    mockSnackBar = {
      open: jest.fn()
    } as unknown as jest.Mocked<MatSnackBar>;    

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        RouterTestingModule.withRoutes([]),
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: MatSnackBar, useValue: mockSnackBar },
      ],
    })
      .compileComponents();

    mockRouter = TestBed.inject(Router) as jest.Mocked<Router>;
    jest.spyOn(mockRouter, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user data on init', () => {
    expect(mockUserService.getById).toHaveBeenCalledWith(mockSessionService.sessionInformation.id.toString());
    expect(component.user).toBe(mockUser);
  });

  it('should call userService.delete and navigate on success', () => {
    component.delete();

    expect(mockUserService.delete).toHaveBeenCalledWith(mockSessionService.sessionInformation.id.toString());
    expect(mockSnackBar.open).toHaveBeenCalledWith('Your account has been deleted !', 'Close', { duration: 3000 });
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should navigate back on back()', () => {
    const spy = jest.spyOn(window.history, 'back');
    
    component.back();

    expect(spy).toHaveBeenCalled();
  });
});
