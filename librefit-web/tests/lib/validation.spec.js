// Importing the required dependencies
import { expect, describe, it } from 'vitest';
import { validateFields } from '$lib/validation.js';

describe('validateFields', () => {
	it('should return undefined when all fields are valid', () => {
		const fields = {
			email: 'test@test.com',
			password: 'Password@123',
			passwordConfirmation: 'Password@123',
			confirmation: true
		};
		const result = validateFields(fields);
		expect(result).toBeUndefined();
	});

	it('should return errors when email is not valid', () => {
		const fields = {
			email: 'test@test',
			password: 'Password@123',
			passwordConfirmation: 'Password@123',
			confirmation: true
		};
		const result = validateFields(fields);
		expect(result.errors).toBeTruthy();
		expect(result.errors[0].field).toBe('email');
	});

	it('should return errors when password is not valid', () => {
		const fields = {
			email: 'test@test.com',
			password: 'Pass',
			passwordConfirmation: 'Pass',
			confirmation: true
		};
		const result = validateFields(fields);
		expect(result.errors).toBeTruthy();
		expect(result.errors[0].field).toBe('password');
	});

	it('should return errors when password confirmation is not valid', () => {
		const fields = {
			email: 'test@test.com',
			password: 'Password@123',
			passwordConfirmation: 'Password@124',
			confirmation: true
		};
		const result = validateFields(fields);
		expect(result.errors).toBeTruthy();
		expect(result.errors[0].field).toBe('passwordConfirmation');
	});

	it('should return errors when tos is not accepted', () => {
		const fields = {
			email: 'test@test.com',
			password: 'Password@123',
			passwordConfirmation: 'Password@123',
			confirmation: false
		};
		const result = validateFields(fields);
		expect(result.errors).toBeTruthy();
		expect(result.errors[0].field).toBe('confirmation');
	});
});
