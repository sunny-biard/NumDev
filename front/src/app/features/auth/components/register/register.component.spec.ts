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
import { RegisterComponent } from './register.component';
import { RegisterRequest } from '../../interfaces/registerRequest.interface';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: jest.Mocked<AuthService>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    mockAuthService = {
      register: jest.fn()
    } as unknown as jest.Mocked<AuthService>;

    mockRouter = {
      navigate: jest.fn()
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        ReactiveFormsModule,
        RouterTestingModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable submit button if form is invalid', () => {
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    
    component.form.setValue({
      firstName: '',
      lastName: '',
      email: '',
      password: ''
    });

    fixture.detectChanges();
    expect(component.form.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();

    component.form.setValue({
      firstName: 'Jean',
      lastName: 'Dupont',
      email: 'invalide',
      password: 'password123'
    });

    fixture.detectChanges();
    expect(component.form.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();

    component.form.setValue({
      firstName: 'Jean',
      lastName: 'Dupont',
      email: 'test@test.fr',
      password: ''
    });

    fixture.detectChanges();
    expect(component.form.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();
  });

  it('should enable submit button when form is valid', () => {
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
    
    component.form.setValue({
      firstName: 'Jean',
      lastName: 'Dupont',
      email: 'test@test.fr',
      password: 'password123'
    });

    fixture.detectChanges();
    expect(component.form.valid).toBeTruthy();
    expect(submitButton.disabled).toBeFalsy();
  });

  it('should submit form and navigate on success', () => {
    mockAuthService.register.mockReturnValue(of(void 0));

    const registerForm: RegisterRequest = {
      firstName: 'Jean',
      lastName: 'Dupont',
      email: 'test@test.fr',
      password: 'password123'
    };

    component.form.setValue(registerForm);
    component.submit();

    expect(mockAuthService.register).toHaveBeenCalledWith(registerForm);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

  it('should set onError to true if register fails', () => {
    mockAuthService.register.mockReturnValue(throwError(() => new Error('Fail')));

    const registerForm: RegisterRequest = {
      firstName: 'Jean',
      lastName: 'Dupont',
      email: 'test@test.fr',
      password: 'password123'
    };

    component.form.setValue(registerForm);
    component.submit();

    expect(mockAuthService.register).toHaveBeenCalledWith(registerForm);
    expect(mockRouter.navigate).not.toHaveBeenCalled();
    expect(component.onError).toBeTruthy();
  });
});
