/** @param {any} fields */
export const validateFields = (fields) => {
	const errors = {};

	const email = String(fields['email']);
	const pwd = String(fields['password']);
	const pwdConfirmation = String(fields['passwordConfirmation']);
	const tosAccepted = Boolean(fields['confirmation']);

	errors['email'] = validateEmail(email);
	errors['password'] = validatePassword(pwd);
	errors['passwordConfirmation'] = validatePasswordConfirmation(pwd, pwdConfirmation);
	errors['confirmation'] = validateTos(tosAccepted);

	if (Object.entries(errors).filter(([_, error]) => error !== null).length > 0) {
		return {
			errors: errors
		};
	}

	return null;
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
