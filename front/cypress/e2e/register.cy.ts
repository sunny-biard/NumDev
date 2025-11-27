describe('Register spec', () => {
    beforeEach(() => {
        cy.visit('/register')
    });

    it('should register successfully', () => {
        cy.intercept('POST', '/api/auth/register', {
        body : {
            statusCode: 200
        }
        })

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

        cy.get('input[formControlName=firstName]').type("Jean")
        cy.get('input[formControlName=lastName]').type("Dupont")
        cy.get('input[formControlName=email]').type("test@test.fr")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

        cy.url().should('include', '/login')

        cy.get('input[formControlName=email]').type("test@test.fr")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

        cy.url().should('include', '/sessions')

        cy.wait('@session').then(({ response }) => {
            expect(response.statusCode).to.eq(200)
        })
    })

    it('should show error message on invalid register', () => {
        cy.get('input[formControlName=firstName]').type("Jean")
        cy.get('input[formControlName=lastName]').type("Dupont")
        cy.get('input[formControlName=email]').type("yoga@studio.com")
        cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

        cy.get('.error').should('be.visible').and('contain', 'An error occurred')
    });
});