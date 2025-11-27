module.exports = {
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.(ts|js|html)$': ['jest-preset-angular', {
      tsconfig: '<rootDir>/tsconfig.spec.json',
      stringifyContentPathRegex: '\\.html$',
      diagnostics: false
    }]
  },
  moduleNameMapper: {
    '^src/(.*)$': '<rootDir>/src/$1',
    '@core/(.*)': '<rootDir>/src/app/core/$1'
  },
  transformIgnorePatterns: ['node_modules/(?!@angular|rxjs)'],
  moduleFileExtensions: ['ts', 'html', 'js', 'json'],
  testPathIgnorePatterns: ['<rootDir>/node_modules/', '<rootDir>/dist/'],
  coverageDirectory: './coverage/jest',
  collectCoverage: true,
collectCoverageFrom: [
    'src/app/**/*.{ts,js}',
    '!src/**/*.spec.ts',
    '!src/**/*.integration.spec.ts',
    '!**/*.module.ts',
    '!**/main.ts',
    '!**/polyfills.ts',
    '!**/test.ts',
    '!**/environments/**',
    '!src/app/guards/**',
    '!src/app/interceptors/**'
  ],
  coverageThreshold: {
    global: {
      statements: 80,
      branches: 80,
      functions: 80,
      lines: 80
    }
  },
  modulePaths: ['<rootDir>'],
  moduleDirectories: ['node_modules'],
  roots: ['<rootDir>']
};