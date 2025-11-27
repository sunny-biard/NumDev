import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionsService', () => {
  let service: SessionApiService;
  let mockHttpClient: jest.Mocked<HttpClient>;

  const mockSession: Session = {
      id: 1, 
      name: 'Test Session 1',
      description: 'Description',
      date: new Date(),
      teacher_id: 1,
      users: []
  }

  beforeEach(() => {
    mockHttpClient = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    TestBed.configureTestingModule({
      imports:[],
      providers: [{ provide: HttpClient, useValue: mockHttpClient }]
    });
    
    service = TestBed.inject(SessionApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call all()', () => {
    service.all();
    expect(mockHttpClient.get).toHaveBeenCalledWith('api/session');
  });

  it('should call detail()', () => {
    service.detail('1');
    expect(mockHttpClient.get).toHaveBeenCalledWith('api/session/1');
  });

  it('should call create()', () => {
    service.create(mockSession);
    expect(mockHttpClient.post).toHaveBeenCalledWith('api/session', mockSession);
  });

  it('should call update()', () => {
    service.update('1', mockSession);
    expect(mockHttpClient.put).toHaveBeenCalledWith('api/session/1', mockSession);
  });

  it('should call delete()', () => {
    service.delete('1');
    expect(mockHttpClient.delete).toHaveBeenCalledWith('api/session/1');
  });

  it('should call participate()', () => {
    service.participate('1', '2');
    expect(mockHttpClient.post).toHaveBeenCalledWith('api/session/1/participate/2', null);
  });

  it('should call unParticipate()', () => {
    service.unParticipate('1', '2');
    expect(mockHttpClient.delete).toHaveBeenCalledWith('api/session/1/participate/2');
  });
});
