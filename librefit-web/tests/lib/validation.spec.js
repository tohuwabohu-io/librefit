// Importing the required dependencies
import { expect, describe, it } from 'vitest';
import { getFieldError, validateAmount, validateFields } from '$lib/validation.js';

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

describe('validateAmount', () => {
	it('should return error when amount is null or undefined', () => {
		expect(validateAmount(null)).toBe('Please enter a valid amount.');
		expect(validateAmount(undefined)).toBe('Please enter a valid amount.');
	});

	it('should return error when amount is less than or equal to 0', () => {
		expect(validateAmount(0)).toBe('Please enter a valid amount.');
		expect(validateAmount(-1)).toBe('Please enter a valid amount.');
	});

	it('should not return error when amount is greater than 0', () => {
		expect(validateAmount(1)).toBeUndefined();
	});
});

describe('getFieldError', () => {
	const mockedResponse = {
		success: false,
		errors: [
			{ field: 'age', message: 'Invalid age.' },
			{ field: 'name', message: 'Invalid name.' }
		]
	};

	it('should return undefined when errorResponse is null or undefined', () => {
		expect(getFieldError(null, 'age')).toBeUndefined();
		expect(getFieldError(undefined, 'age')).toBeUndefined();
	});

	it('should return undefined when success property in errorResponse is true', () => {
		expect(getFieldError({ success: true }, 'age')).toBeUndefined();
	});

	it('should return undefined when there is no error for the specified field', () => {
		expect(getFieldError(mockedResponse, 'address')).toBeUndefined();
	});

	it('should return error message when there is an error for the specified field', () => {
		expect(getFieldError(mockedResponse, 'age')).toBe('Invalid age.');
		expect(getFieldError(mockedResponse, 'name')).toBe('Invalid name.');
	});
});
