import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { AuthService } from './auth.service';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';

describe('AuthService', () => {
    let service: AuthService;
    let mockHttpClient: jest.Mocked<HttpClient>;

    const MockLoginRequest: LoginRequest = {
        email: 'test@test.fr',
        password: 'password123'
    }

    const MockRegisterRequest: RegisterRequest = {
        email: 'test@test.fr',
        firstName: 'Jean',
        lastName: 'Dupont',
        password: 'password123'
    }

    beforeEach(() => {
        mockHttpClient = {
            post: jest.fn() 
        } as unknown as jest.Mocked<HttpClient>;

        TestBed.configureTestingModule({
            imports:[],
            providers: [{ provide: HttpClient, useValue: mockHttpClient }]
        });
        
        service = TestBed.inject(AuthService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should register a user', () => {
        service.register(MockRegisterRequest);
        expect(mockHttpClient.post).toHaveBeenCalledWith('api/auth/register', MockRegisterRequest);
    });

    it('should login a user', () => {
        service.login(MockLoginRequest);
        expect(mockHttpClient.post).toHaveBeenCalledWith('api/auth/login', MockLoginRequest);
    });
});
