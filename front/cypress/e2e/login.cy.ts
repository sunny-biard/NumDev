describe('Login spec', () => {
  beforeEach(() => {
    cy.visit('/login')
  });

  it('should login successfully', () => {
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

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')

    cy.wait('@session').then(({ response }) => {
      expect(response.statusCode).to.eq(200)
    })
  })

  it('should logout successfully', () => {
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

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')

    cy.get('span:contains("Logout")').click()

    cy.url().should('include', '/')
  });

  it('should show error message on invalid login', () => {
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"wrong"}{enter}{enter}`)

    cy.get('.error').should('be.visible').and('contain', 'An error occurred')
  });
});