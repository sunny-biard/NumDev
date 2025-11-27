describe('Sessions spec as a admin', () => {
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

        cy.intercept('GET', '/api/session', {
            body: [{
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },{
                id: 2,
                name: "Yoga Friday",
                date: "2025-11-14T00:00:00.000+00:00",
                teacher_id: 2,
                description: "Yoga Test 2",
                users: [],
                createdAt: "2025-11-07T19:03:49",
                updatedAt: "2025-11-09T19:03:49"
            }]
        }).as('session')

        cy.intercept('GET', '/api/session/1', {
            body: {
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [1],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },
        })

        cy.intercept('GET', '/api/teacher', {
            body: [{
                id: 1,
                lastName: "DELAHAYE",
                firstName: "Margot",
                createdAt: "2025-11-07T22:46:58",
                updatedAt: "2025-11-07T22:46:58"
            }, {
                id: 2,
                lastName: "THIERCELIN",
                firstName: "Hélène",
                createdAt: "2025-11-07T22:46:58",
                updatedAt: "2025-11-07T22:46:58"
            }]
        })

        cy.intercept('GET', '/api/teacher/1', {
            body: {
                id: 1,
                lastName: "DELAHAYE",
                firstName: "Margot",
                createdAt: "2025-05-20T19:03:49",
                updatedAt: "2025-05-20T19:03:49"
            },
        })

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

        cy.get('mat-card-title').contains('Yoga Thursday').should('be.visible')
        cy.get('mat-card-subtitle').contains('Session on November 13, 2025').should('be.visible')
        cy.get('p').contains('Yoga Test 1').should('be.visible')
        cy.get('mat-card-title').contains('Yoga Friday').should('be.visible')
        cy.get('mat-card-subtitle').contains('Session on November 14, 2025').should('be.visible')
        cy.get('p').contains('Yoga Test 2').should('be.visible')
    });

    it('should show session details', () => {
        cy.get('button').contains('Detail').first().click()

        cy.url().should('include', '/sessions/detail/1')

        cy.get('h1').contains('Yoga Thursday').should('be.visible')
        cy.get('mat-card-subtitle').contains('Margot DELAHAYE').should('be.visible')
        cy.get('mat-card-content').contains('1 attendees').should('be.visible')
        cy.get('mat-card-content').contains('November 13, 2025').should('be.visible')
        cy.get('mat-card-content').contains('Yoga Test 1').should('be.visible')
        cy.get('mat-card-content').contains('November 10, 2025').should('be.visible')
        cy.get('mat-card-content').contains('November 11, 2025').should('be.visible')
    });

    it('should go back successfully', () => {
        cy.get('button').contains('Detail').first().click()

        cy.url().should('include', '/sessions/detail/1')

        cy.get('button').contains('arrow_back').click()

        cy.url().should('include', '/sessions')
    });

    it('should create session successfully', () => {
        cy.intercept('POST', '/api/session', {
            body: {
                id: 1,
                name: "Yoga Saturday",
                date: "2025-11-16T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test Saturday",
                users: [],
                createdAt: "2025-11-14T16:31:05.811883",
                updatedAt: "2025-11-14T16:31:05.81734"
            }
        })

        cy.get('button').contains('Create').first().click()

        cy.url().should('include', '/sessions/create')

        cy.get('input[formControlName=name]').type("Yoga Saturday")
        cy.get('input[formControlName=date]').type("2025-11-16")
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').contains('Margot DELAHAYE').click()
        cy.get('textarea[formControlName=description]').type("Yoga Test Saturday")

        cy.get('button[type=submit]').click()

        cy.url().should('include', '/sessions')
    });

    it('should edit session successfully', () => {
        cy.intercept('PUT', '/api/session/1', {
            body: {
                id: 1,
                name: "Yoga Sunday",
                date: "2025-11-17T00:00:00.000+00:00",
                teacher_id: 2,
                description: "Yoga Test Sunday",
                users: [],
                createdAt: null,
                updatedAt: "2025-11-14T16:45:05.81734"
            }
        })

        cy.get('button').contains('Edit').first().click()

        cy.url().should('include', '/sessions/update/1')

        cy.get('input[formControlName=name]').clear().type("Yoga Sunday")
        cy.get('input[formControlName=date]').clear().type("2025-11-17")
        cy.get('mat-select[formControlName=teacher_id]').click()
        cy.get('mat-option').contains('Hélène THIERCELIN').click()
        cy.get('textarea[formControlName=description]').clear().type("Yoga Test Sunday")

        cy.get('button[type=submit]').click()

        cy.url().should('include', '/sessions')
    });
    
    it('should delete session successfully', () => {
        cy.intercept('DELETE', '/api/session/1', {
            statusCode: 200
        })

        cy.get('button').contains('Detail').first().click()

        cy.url().should('include', '/sessions/detail/1')

        cy.get('button').contains('Delete').click()

        cy.url().should('include', '/sessions')
    });
});

describe('Sessions spec as a user', () => {
    beforeEach(() => {
        cy.visit('/login')

        cy.intercept('POST', '/api/auth/login', {
            body: {
                id: 1,
                username: 'userName',
                firstName: 'firstName',
                lastName: 'lastName',
                admin: false
            },
        })

        cy.intercept('GET', '/api/session', {
            body: [{
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },{
                id: 2,
                name: "Yoga Friday",
                date: "2025-11-14T00:00:00.000+00:00",
                teacher_id: 2,
                description: "Yoga Test 2",
                users: [],
                createdAt: "2025-11-07T19:03:49",
                updatedAt: "2025-11-09T19:03:49"
            }]
        }).as('session')

        cy.intercept('GET', '/api/teacher/1', {
            body: {
                id: 1,
                lastName: "DELAHAYE",
                firstName: "Margot",
                createdAt: "2025-05-20T19:03:49",
                updatedAt: "2025-05-20T19:03:49"
            },
        })

        cy.intercept('GET', '/api/user/1', {
            body: {
                id: 1,
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

        cy.get('mat-card-title').contains('Yoga Thursday').should('be.visible')
        cy.get('mat-card-subtitle').contains('Session on November 13, 2025').should('be.visible')
        cy.get('p').contains('Yoga Test 1').should('be.visible')
        cy.get('mat-card-title').contains('Yoga Friday').should('be.visible')
        cy.get('mat-card-subtitle').contains('Session on November 14, 2025').should('be.visible')
        cy.get('p').contains('Yoga Test 2').should('be.visible')
    });

    it('should show session details', () => {
        cy.intercept('GET', '/api/session/1', {
            body: {
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [1,2],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },
        })

        cy.get('button').contains('Detail').first().click()

        cy.url().should('include', '/sessions/detail/1')

        cy.get('h1').contains('Yoga Thursday').should('be.visible')
        cy.get('mat-card-subtitle').contains('Margot DELAHAYE').should('be.visible')
        cy.get('mat-card-content').contains('2 attendees').should('be.visible')
        cy.get('mat-card-content').contains('November 13, 2025').should('be.visible')
        cy.get('mat-card-content').contains('Yoga Test 1').should('be.visible')
        cy.get('mat-card-content').contains('November 10, 2025').should('be.visible')
        cy.get('mat-card-content').contains('November 11, 2025').should('be.visible')
    });

    it('should go back successfully', () => {
        cy.intercept('GET', '/api/session/1', {
            body: {
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [1,2],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },
        })

        cy.get('button').contains('Detail').first().click()

        cy.url().should('include', '/sessions/detail/1')

        cy.get('button').contains('arrow_back').click()

        cy.url().should('include', '/sessions')
    });

    it('should participate to session successfully', () => {
        cy.intercept('GET', '/api/session/1', {
            body: {
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [2],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },
        })

        cy.intercept('POST', '/api/session/1/participate/1', {
            statusCode: 200
        })

        cy.intercept('DELETE', '/api/session/1/participate/1', {
            statusCode: 200
        })

        cy.get('button').contains('Detail').first().click()

        cy.url().should('include', '/sessions/detail/1')

        cy.intercept('GET', '/api/session/1', {
            body: {
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [1,2],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },
        })

        cy.get('button').contains('Participate').click()

        cy.get('mat-card-content').contains('2 attendees').should('be.visible')

        cy.get('button').contains('Do not participate').should('be.visible')

        cy.intercept('GET', '/api/session/1', {
            body: {
                id: 1,
                name: "Yoga Thursday",
                date: "2025-11-13T00:00:00.000+00:00",
                teacher_id: 1,
                description: "Yoga Test 1",
                users: [2],
                createdAt: "2025-11-10T19:03:49",
                updatedAt: "2025-11-11T19:03:49"
            },
        })

        cy.get('button').contains('Do not participate').click()

        cy.get('mat-card-content').contains('1 attendees').should('be.visible')

        cy.get('button').contains('Participate').should('be.visible')
    });
});