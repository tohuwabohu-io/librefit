import { convertFormDataToJson } from './util.js';
import { invoke } from '@tauri-apps/api/core';
import type { LibreUser } from '../model';

export const updateProfile = async (formData: FormData): Promise<LibreUser> => {
	const userData = convertFormDataToJson(formData);

	return invoke('update_user', {
		userName: userData.username,
		userAvatar: userData.avatar
	});
};
