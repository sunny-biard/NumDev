import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { expect } from '@jest/globals';
import { HttpClientModule } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let mockSessionService: jest.Mocked<SessionService>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    mockSessionService = {
      sessionInformation: { 
        id: 1, 
        admin: false
      },
      $isLogged: jest.fn().mockReturnValue(of(true)),
      logOut: jest.fn(),
    } as unknown as jest.Mocked<SessionService>;

    await TestBed.configureTestingModule({
      declarations: [AppComponent],
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatToolbarModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    mockRouter = TestBed.inject(Router) as jest.Mocked<Router>;
    jest.spyOn(mockRouter, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should return observable true from $isLogged()', (done) => {
    component.$isLogged().subscribe(value => {
      expect(value).toBe(true);
      done();
    });
  });

  it('should call logout and navigate to home', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const component = fixture.componentInstance;

    component.logout();

    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['']);
  });
});
