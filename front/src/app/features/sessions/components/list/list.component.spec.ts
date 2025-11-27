import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import { Session } from '../../interfaces/session.interface';
import { of } from 'rxjs';
import { SessionApiService } from '../../services/session-api.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  const mockSessions: Session[] = [
    {
      id: 1, 
      name: 'Test Session 1',
      description: 'Description',
      date: new Date(),
      teacher_id: 1,
      users: []
    },
    {
      id: 2, 
      name: 'Test Session 2',
      description: 'Description',
      date: new Date(),
      teacher_id: 2,
      users: []
    },
  ];

  const mockSessionApiService = {
    all: jest.fn().mockReturnValue(of(mockSessions))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render a list of sessions', () => {
    const cards = fixture.nativeElement.querySelectorAll('.item');
    expect(cards.length).toBe(mockSessions.length);

    const firstCardTitle = cards[0].querySelector('mat-card-title');
    expect(firstCardTitle?.textContent).toContain('Test Session 1');
  });

  it('should display "Create" button if user is admin', () => {
    const button = fixture.nativeElement.querySelector('button[routerLink="create"]');
    expect(button).toBeTruthy();
  });

  it('should display Detail and Edit buttons if user is admin', () => {
    const actionButtons = fixture.nativeElement.querySelectorAll('mat-card-actions button span');
    const labels = Array.from(actionButtons as NodeListOf<HTMLElement>).map(btn => btn.textContent?.trim());
    expect(labels).toContain('Detail');
    expect(labels).toContain('Edit');
  });

    it('should not display Create button if user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('button[routerLink="create"]');
    expect(button).toBeFalsy();
  });

  it('should not display Edit button if user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    const button = fixture.nativeElement.querySelector('mat-icon[name="edit"]');
    expect(button).toBeFalsy();
  });
});
