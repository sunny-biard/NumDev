import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockSessionApiService: jest.Mocked<SessionApiService>;
  let mockRouter: jest.Mocked<Router>;
  let mockSnackBar: jest.Mocked<MatSnackBar>;
  let mockActivatedRoute: jest.Mocked<ActivatedRoute>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  beforeEach(async () => {
    mockSessionApiService = {
      create: jest.fn(),
      update: jest.fn(),
      detail: jest.fn()
    } as unknown as jest.Mocked<SessionApiService>;

    mockSnackBar = {
      open: jest.fn()
    } as unknown as jest.Mocked<MatSnackBar>;

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule, 
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    mockRouter = TestBed.inject(Router) as jest.Mocked<Router>;
    jest.spyOn(mockRouter, 'navigate').mockResolvedValue(true);

    mockActivatedRoute = TestBed.inject(ActivatedRoute) as jest.Mocked<ActivatedRoute>;
    jest.spyOn(mockActivatedRoute.snapshot.paramMap, 'get').mockReturnValue('1');

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable submit button if form is invalid', () => {
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');

    component.sessionForm?.setValue({
      name: '',
      date: '',
      teacher_id: null,
      description: '',
    });


    fixture.detectChanges();
    expect(component.sessionForm?.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();

    component.sessionForm?.setValue({
      name: '',
      date: '',
      teacher_id: 1,
      description: '',
    });

    fixture.detectChanges();
    expect(component.sessionForm?.valid).toBeFalsy();
    expect(submitButton.disabled).toBeTruthy();
  });

  it('should enable submit button when form is valid', () => {
    const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');

    component.sessionForm?.setValue({
      name: 'Test Session 1',
      date: new Date(),
      teacher_id: 1,
      description: 'Description'
    });

    fixture.detectChanges();
    expect(component.sessionForm?.valid).toBeTruthy();
    expect(submitButton.disabled).toBeFalsy();
  });

  it('should call session-api.create and navigate on success', () => {
    const session = {
      name: 'Test Session 1',
      date: new Date(),
      teacher_id: 1,
      description: 'Description'
    };

    component.sessionForm?.setValue(session);

    const sessionResponse: Session = {
      ...session,
      users: []
    };

    mockSessionApiService.create.mockReturnValue(of(sessionResponse));

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalledWith(session);
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should call session-api.update and navigate on success', () => {
    const session = {
      name: 'Test Session 1',
      date: new Date().toISOString().split('T')[0],
      teacher_id: 1,
      description: 'Description'
    };

    component.sessionForm?.setValue(session);

    const sessionResponse: Session = {
      ...session,
      date: new Date(session.date),
      users: []
    };

    mockSessionApiService.update.mockReturnValue(of(sessionResponse));
    mockSessionApiService.detail.mockReturnValue(of(sessionResponse));

    jest.spyOn(component['router'], 'url', 'get').mockReturnValue('/sessions/update/1');
    component.ngOnInit();

    expect(component.onUpdate).toBe(true);
    expect(component['id']).toBe('1');

    component.submit();

    expect(mockSessionApiService.update).toHaveBeenCalledWith(component['id'], session);
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should redirect to sessions list when user is not admin', () => {  
    mockSessionService.sessionInformation.admin = false;

    jest.spyOn(component['router'], 'url', 'get').mockReturnValue('/sessions/create');
    component.ngOnInit();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});