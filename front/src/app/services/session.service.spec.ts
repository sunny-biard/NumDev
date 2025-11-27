import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    token: '123',
    type: 'user',
    id: 1,
    username: 'dupont123',
    firstName: 'Jean',
    lastName: 'Dupont',
    admin: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

    it('should emit false by default via $isLogged()', (done) => {
    service.$isLogged().subscribe(value => {
      expect(value).toBe(false);
      done();
    });
  });

  it('should log in the user and emit true via $isLogged()', (done) => {
    service.logIn(mockUser);

    expect(service.sessionInformation).toEqual(mockUser);
    expect(service.isLogged).toBe(true);

    service.$isLogged().subscribe(value => {
      expect(value).toBe(true);
      done();
    });
  });

  it('should log out the user and emit false via $isLogged()', (done) => {
    service.logIn(mockUser);
    expect(service.isLogged).toBe(true);

    service.logOut();

    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);

    service.$isLogged().subscribe(value => {
      expect(value).toBe(false);
      done();
    });
  });
});

