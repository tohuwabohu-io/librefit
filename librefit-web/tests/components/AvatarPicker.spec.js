import { fireEvent, screen, render } from '@testing-library/svelte';
import { describe, expect, it, vi } from 'vitest';
import AvatarPicker from '$lib/components/AvatarPicker.svelte';

const getAvatar = (fileName) => `/assets/images/avatars/${fileName}`;
const files = ['file1', 'file2', 'file3'];

/**
 * @vitest-environment jsdom
 */
describe('AvatarPicker.svelte', () => {
	it('should trigger avatarClicked function when Avatar is clicked', async () => {
		let chosenFile;
		const chooseAvatarMock = vi.fn((event) => {
			chosenFile = event.detail;
		});

		const { component } = render(AvatarPicker, {
			props: { fileList: files }
		});

		component.$on('chooseAvatar', chooseAvatarMock);

		const buttons = screen.getAllByRole('button', { name: 'choose' });

		// loop through buttons with index
		buttons.forEach((button, index) => {
			fireEvent.click(buttons[index]);

			expect(chooseAvatarMock).toHaveBeenCalledTimes(index + 1);
			expect(chosenFile).toEqual({
				avatar: getAvatar(files[index])
			});
		});
	});

	it('should preselect current user avatar', async () => {
		const fakeUser = {
			avatar: 'file3'
		};

		const { component } = render(AvatarPicker, {
			props: { fileList: files, chosen: fakeUser.avatar }
		});

		const avatarImg = document.querySelector(`img[src$="${fakeUser.avatar}" i]`);
		const avatarFigure = avatarImg.parentElement;

		expect(avatarFigure.classList).toContain('!border-primary-500');
	});
});
