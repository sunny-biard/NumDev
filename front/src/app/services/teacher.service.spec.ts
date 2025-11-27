import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let mockHttpClient: jest.Mocked<HttpClient>;

  beforeEach(() => {
    mockHttpClient = {
      get: jest.fn()
    } as unknown as jest.Mocked<HttpClient>;

    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ],
      providers: [{ provide: HttpClient, useValue: mockHttpClient }]
    });
    service = TestBed.inject(TeacherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call all()', () => {
    service.all();
    expect(mockHttpClient.get).toHaveBeenCalledWith('api/teacher');
  });

  it('should call detail()', () => {
    service.detail('1');
    expect(mockHttpClient.get).toHaveBeenCalledWith('api/teacher/1');
  });
});
