describe('Not Found spec', () => {
    it('should show 404 page', () => {
        cy.visit('/azerty')

        cy.get('h1').contains('Page not found !').should('be.visible')
    });
});