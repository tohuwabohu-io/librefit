import { parseStringAsDate } from '$lib/date.js';
import { isAfter } from 'date-fns';

/** @type RegExp */
const emailRegex = /^\S+@\S+\.\S+$/;

/**
 * @param {any} fields
 * @return {ErrorResponse | undefined}
 */
export const validateFields = (fields) => {
	const inputValidation = {};

	const email = String(fields['email']);
	const pwd = String(fields['password']);
	const pwdConfirmation = String(fields['passwordConfirmation']);
	const tosAccepted = Boolean(fields['confirmation']);

	inputValidation['email'] = validateEmail(email);
	inputValidation['password'] = validatePassword(pwd);
	inputValidation['passwordConfirmation'] = validatePasswordConfirmation(pwd, pwdConfirmation);
	inputValidation['confirmation'] = validateTos(tosAccepted);

	const inputErrors = Object.entries(inputValidation).filter(([_, error]) => error !== null);

	if (inputErrors.length > 0) {
		/** @type {Array<ErrorDescription>} */
		const errorDescriptions = inputErrors.map(([inputField, validationMessage]) => {
			return {
				field: inputField,
				message: validationMessage
			};
		});

		return {
			errors: errorDescriptions
		};
	}

	return undefined;
};

/**
 * @param {String} email
 * @returns {String | null}
 */
export const validateEmail = (email) => {
	if (!email || !email.trim().match(emailRegex)) {
		return 'Please enter a valid email address.';
	}

	return null;
};

/**
 * @param {String} pwd
 * @returns {String | null}
 */
export const validatePassword = (pwd) => {
	if (pwd === null || pwd.length < 6) {
		return 'Chosen password must be at least 6 characters long.';
	}

	return null;
};

/**
 * @param {String} pwd
 * @param {String} pwdConfirmation
 * @returns {String | null}
 */
export const validatePasswordConfirmation = (pwd, pwdConfirmation) => {
	if (pwdConfirmation === null || pwd !== pwdConfirmation) {
		return 'Passwords do not match.';
	}

	return null;
};

/**
 *
 * @param {Boolean | undefined} tosAccepted
 * @returns {String | null}
 */
export const validateTos = (tosAccepted) => {
	if (Boolean(tosAccepted) !== true || tosAccepted === undefined) {
		return 'Please accept our terms and conditions.';
	}

	return null;
};

/**
 * @param amount
 * @return {string}
 */
export const validateAmount = (amount) => {
	if (!amount || amount <= 0) {
		return 'Please enter a valid amount.';
	}
};

/**
 * @param errorResponse {ErrorResponse | { success: boolean} | FormData}
 * @param fieldName {String}
 * @return {undefined | String}
 */
export const getFieldError = (errorResponse, fieldName) => {
	if (!errorResponse || errorResponse.success) {
		return undefined;
	}

	/** @type {Array<ErrorDescription>} */
	const descriptions = errorResponse.errors;

	/** @type {ErrorDescription} */
	const description = descriptions.filter((description) => description.field === fieldName)[0];

	return description ? description.message : undefined;
};
/**
 * @param target {CalorieTarget}
 * @returns {Object}
 */
export const validateCalorieTarget = (target) => {
	let endDateValidation = validateEndDate(target.startDate, target.endDate);
	let targetCaloriesValidation = validateTrackerAmount({ value: target.targetCalories });
	let maximumCaloriesValidation = validateTrackerAmount({ value: target.maximumCalories });

	if (maximumCaloriesValidation.valid) {
		maximumCaloriesValidation =
			target.targetCalories > target.maximumCalories
				? { valid: false, errorMessage: 'Maximum calories should be greater than target calories.' }
				: maximumCaloriesValidation;
	}

	return {
		endDate: endDateValidation,
		targetCalories: targetCaloriesValidation,
		maximumCalories: maximumCaloriesValidation
	};
};
/**
 * @param target {WeightTarget}
 * @returns {any}
 */
export const validateWeightTarget = (target) => {
	let endDateValidation = validateEndDate(target.startDate, target.endDate);
	let initialWeightMessage = validateTrackerAmount({ value: target.initialWeight });
	let targetWeightMessage = validateTrackerAmount({ value: target.targetWeight });

	return {
		endDate: endDateValidation,
		initialWeight: initialWeightMessage,
		targetWeight: targetWeightMessage
	};
};

export const validateEndDate = (startDateStr, endDateStr) => {
	let startDate = parseStringAsDate(startDateStr);
	let endDate = parseStringAsDate(endDateStr);

	if (isAfter(startDate, endDate)) {
		return {
			valid: false,
			errorMessage: 'End date must be after start date.'
		};
	}

	return { valid: true };
};

export const validateTrackerAmount = (detail) => {
	if (!detail.value || detail.value <= 0) {
		return {
			valid: false,
			errorMessage: `${detail.label ? detail.label : 'Value'} must be greater than zero.`
		};
	}

	return { valid: true };
};
