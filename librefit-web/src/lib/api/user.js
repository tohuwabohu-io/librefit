import { convertFormDataToJson, proxyFetch } from '$lib/api/util.js';
import { invoke } from '@tauri-apps/api/core';

/**
 * @param formData {FormData}
 * @return {Promise}
 */
export const updateProfile = async (formData) => {
	const userData = convertFormDataToJson(formData);

	/** @type {LibreUser} */
	const user = {
		name: userData.username,
		avatar: userData.avatar
	};

	return invoke('update_user', { 
		userName: userData.username, 
		userAvatar: userData.avatar 
	});
};




