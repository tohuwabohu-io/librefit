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
		const errorDescriptions = inputValidation.map(([inputField, validationMessage]) => {
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
	if (email === null || email.indexOf('@') <= 0 || email.length <= 4) {
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
 * @param errorResponse {ErrorResponse}
 * @param fieldName {String}
 * @return {undefined | String}
 */
export const getFieldError = (errorResponse, fieldName) => {
	if (!errorResponse) {
		return undefined;
	}

	/** @type {Array<ErrorDescription>} */
	const descriptions = errorResponse.errors;

	/** @type {ErrorDescription} */
	const description = descriptions.filter((description) => description.field === fieldName)[0];

	return description ? description.message : undefined;
};
