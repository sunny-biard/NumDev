import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { expect, jest } from '@jest/globals';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { LoginComponent } from './login.component';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginRequest } from 'src/app/features/auth/interfaces/loginRequest.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: jest.Mocked<AuthService>;
  let mockRouter: jest.Mocked<Router>;
  let mockSessionService: jest.Mocked<SessionService>;

  beforeEach(async () => {
    mockAuthService = {
      login: jest.fn()
    } as unknown as jest.Mocked<AuthService>;

    mockRouter = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    mockSessionService = {
      logIn: jest.fn()
    } as unknown as jest.Mocked<SessionService>;

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService }
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable submit button if form is invalid', () => {
    const emailControl = component.form.get('email');
    const passwordControl = component.form.get('password');
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');

    emailControl?.setValue('');
    passwordControl?.setValue('');

    fixture.detectChanges();
    expect(component.form.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();

    emailControl?.setValue('invalide');
    passwordControl?.setValue('password123');

    fixture.detectChanges();
    expect(component.form.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();

    emailControl?.setValue('test@test.fr');
    passwordControl?.setValue('');

    fixture.detectChanges();
    expect(component.form.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();
  });

  it('should enable submit button when form is valid', () => {
    const emailControl = component.form.get('email');
    const passwordControl = component.form.get('password');
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');

    emailControl?.setValue('test@test.fr');
    passwordControl?.setValue('password123');

    fixture.detectChanges();
    expect(component.form.valid).toBeTruthy();
    expect(submitButton.disabled).toBeFalsy();
  });

  it('should call authService.login and navigate on success', () => {
    const mockResponse: SessionInformation = {
      token: 'abc123',
      type: 'teacher',
      id: 1,
      username: 'Jean',
      firstName: 'Jean',
      lastName: 'Dupont',
      admin: false
    };

    mockAuthService.login.mockReturnValue(of(mockResponse));

    const loginForm: LoginRequest = { email: 'test@test.fr', password: 'password123' };
    component.form.setValue(loginForm);
    component.submit();

    expect(mockAuthService.login).toHaveBeenCalledWith(loginForm);
    expect(mockSessionService.logIn).toHaveBeenCalledWith(mockResponse);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBeFalsy();
  });

  it('should set onError to true when login fails', () => {
    mockAuthService.login.mockReturnValue(throwError(() => new Error('Login failed')));

    const loginForm: LoginRequest = { email: 'test@test.fr', password: 'wrong' };
    component.form.setValue(loginForm);
    component.submit();

    expect(mockAuthService.login).toHaveBeenCalledWith(loginForm);
    expect(component.onError).toBeTruthy();
    expect(mockSessionService.logIn).not.toHaveBeenCalled();
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });

  it('should toggle password visibility when hide changes', () => {
    expect(component.hide).toBeTruthy();
    component.hide = !component.hide;
    expect(component.hide).toBeFalsy();
  });
});
