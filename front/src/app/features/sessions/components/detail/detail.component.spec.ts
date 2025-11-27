import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { SessionApiService } from '../../services/session-api.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TeacherService } from 'src/app/services/teacher.service';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let mockSessionApiService: jest.Mocked<SessionApiService>;
  let mockTeacherService: jest.Mocked<TeacherService>;
  let mockRouter: jest.Mocked<Router>;
  let mockSnackBar: jest.Mocked<MatSnackBar>;
  let mockActivatedRoute: jest.Mocked<ActivatedRoute>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  }

  const mockSession: Session = {
    id: 1,
    name: 'Test Session 1',
    date: new Date(),
    description: 'Description',
    teacher_id: 2,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockTeacher: Teacher = {
    id: 2,
    lastName: 'Dupont',
    firstName: 'Jean',
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(async () => {
    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(mockSession)),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({}))
    } as unknown as jest.Mocked<SessionApiService>;

    mockSnackBar = {
      open: jest.fn()
    } as unknown as jest.Mocked<MatSnackBar>;

    mockTeacherService = {
      detail: jest.fn().mockReturnValue(of(mockTeacher))
    } as unknown as jest.Mocked<TeacherService>;

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent], 
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: TeacherService, useValue: mockTeacherService }
      ],
    }).compileComponents();

    mockRouter = TestBed.inject(Router) as jest.Mocked<Router>;
    jest.spyOn(mockRouter, 'navigate').mockResolvedValue(true);

    mockActivatedRoute = TestBed.inject(ActivatedRoute) as jest.Mocked<ActivatedRoute>;
    jest.spyOn(mockActivatedRoute.snapshot.paramMap, 'get').mockReturnValue('1');

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display Delete button if user is admin', () => {
    const button = fixture.nativeElement.querySelector('button span');
    expect(button.textContent).toContain('Delete');
  });

  it('should not display Participate button if user is admin', () => {
    const actionButtons = fixture.nativeElement.querySelectorAll('button span');
    const labels = Array.from(actionButtons as NodeListOf<HTMLElement>).map(btn => btn.textContent?.trim());
    expect(labels).not.toContain('Participate');
    expect(labels).not.toContain('Do not participate');
  });

  it('should display Participate button if user is not admin and user has not participated', () => {
    mockSessionService.sessionInformation.admin = false;
    mockSession.users = [];

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button span');
    expect(button.textContent).toContain('Participate');
  });

  it('should display Do not participate button if user is not admin and user has participated', () => {
    mockSessionService.sessionInformation.admin = false;
    mockSession.users = [1];

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button span');
    expect(button.textContent).toContain('Do not participate');
  });

  it('should not display Delete button if user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    const actionButtons = fixture.nativeElement.querySelectorAll('button span');
    const labels = Array.from(actionButtons as NodeListOf<HTMLElement>).map(btn => btn.textContent?.trim());
    expect(labels).not.toContain('Delete');
  });

  it('should call session-api.delete and navigate on success', () => {
    component.delete();

    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should call session-api.participate and refresh session on success', () => {
    mockSessionService.sessionInformation.admin = false;

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    component.participate();

    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', mockSessionService.sessionInformation.id.toString());
    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(3);
  });

  it('should call session-api.unParticipate and refresh session on success', () => {
    mockSessionService.sessionInformation.admin = false;
    mockSession.users = [1];

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    component.unParticipate();

    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', mockSessionService.sessionInformation.id.toString());
    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(3);
  });

  it('should navigate back on back()', () => {
    const spy = jest.spyOn(window.history, 'back');

    component.back();

    expect(spy).toHaveBeenCalled();
  });
});

