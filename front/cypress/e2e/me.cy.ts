describe('Me spec as a admin', () => {
    beforeEach(() => {
        cy.visit('/login')

        cy.intercept('POST', '/api/auth/login', {
            body: {
                id: 1,
                username: 'userName',
                firstName: 'firstName',
                lastName: 'lastName',
                admin: true
            },
        })

        cy.intercept(
        {
            method: 'GET',
            url: '/api/session',
        },
        []).as('session')

        cy.intercept('GET', '/api/user/1', {
        body: {
            id: 1,
            email: "yoga@studio.com",
            lastName: "Admin",
            firstName: "Admin",
            admin: true,
            createdAt: "2025-11-07T15:46:58",
            updatedAt: "2025-11-09T15:46:58"
        },
        })

        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

        cy.url().should('include', '/sessions')
    });

    it('should show accounts details as admin', () => {
        cy.get('span[routerLink="me"]').click()

        cy.url().should('include', '/me')

        cy.get('p').contains('Name').should('be.visible').and('contain', 'Admin ADMIN')
        cy.get('p').contains('Email').should('be.visible').and('contain', 'yoga@studio.com')
        cy.get('p').contains('You are admin').should('be.visible')
        cy.get('p').contains('November 7, 2025').should('be.visible')
        cy.get('p').contains('November 9, 2025').should('be.visible')
    })

    it('should go back successfully', () => {
        cy.get('span[routerLink="me"]').click()

        cy.url().should('include', '/me')

        cy.get('button').contains('arrow_back').click()

        cy.url().should('include', '/sessions')
    });
});

describe('Me spec as a user', () => {
    beforeEach(() => {
        cy.visit('/login')

        cy.intercept('POST', '/api/auth/login', {
            body: {
                id: 2,
                username: 'userName',
                firstName: 'firstName',
                lastName: 'lastName',
                admin: false
            },
        })

        cy.intercept(
        {
            method: 'GET',
            url: '/api/session',
        },
        []).as('session')

        cy.intercept('GET', '/api/user/2', {
        body: {
            id: 2,
            email: "test@test.fr",
            lastName: "Dupont",
            firstName: "Jean",
            admin: false,
            createdAt: "2025-11-07T15:46:58",
            updatedAt: "2025-11-09T15:46:58"
        },
        })

        cy.get('input[formControlName=email]').type("test@test.fr")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

        cy.url().should('include', '/sessions')
    });

    it('should show accounts details as user', () => {
        cy.get('span[routerLink="me"]').click()

        cy.url().should('include', '/me')

        cy.get('p').contains('Name').should('be.visible').and('contain', 'Jean DUPONT')
        cy.get('p').contains('Email').should('be.visible').and('contain', 'test@test.fr')
        cy.get('p').contains('November 7, 2025').should('be.visible')
        cy.get('p').contains('November 9, 2025').should('be.visible')
    })

    it('should go back successfully', () => {
        cy.get('span[routerLink="me"]').click()

        cy.url().should('include', '/me')

        cy.get('button').contains('arrow_back').click()

        cy.url().should('include', '/sessions')
    });

    it('should delete user successfully', () => {
        cy.intercept('DELETE', '/api/user/2', {
            statusCode: 200
        })

        cy.get('span[routerLink="me"]').click()

        cy.url().should('include', '/me')

        cy.get('button').contains('Detail').click()

        cy.url().should('include', '/')
    });
});