import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let mockHttpClient: jest.Mocked<HttpClient>;

  beforeEach(() => {
    mockHttpClient = {
      get: jest.fn(),
      delete: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ],
      providers: [{ provide: HttpClient, useValue: mockHttpClient }]
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call delete()', () => {
    service.delete('1');
    expect(mockHttpClient.delete).toHaveBeenCalledWith('api/user/1');
  });

  it('should call getById()', () => {
    service.getById('1');
    expect(mockHttpClient.get).toHaveBeenCalledWith('api/user/1');
  });
});
