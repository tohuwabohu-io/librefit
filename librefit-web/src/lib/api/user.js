import { convertFormDataToJson } from '$lib/api/util.js';
import { invoke } from '@tauri-apps/api/core';

/**
 * @param formData {FormData}
 * @return {Promise}
 */
export const updateProfile = async (formData) => {
	const userData = convertFormDataToJson(formData);

	return invoke('update_user', {
		userName: userData.username,
		userAvatar: userData.avatar
	});
};
