import { join } from 'path';
import { skeleton } from '@skeletonlabs/tw-plugin';
import { librefitTheme } from './src/librefit-theme.js';

/** @type {import('tailwindcss').Config} */
const config = {
	darkMode: 'class',
	content: [
		'./src/**/*.{html,js,svelte,ts}',
		join(require.resolve('@skeletonlabs/skeleton'), '../**/*.{html,js,svelte,ts}')
	],
	theme: {
		extend: {
			fontFamily: {
				'Soda Berry': ['Soda Berry', 'sans-serif']
			}
		}
	},
	plugins: [
		skeleton({
			themes: { custom: [librefitTheme] }
		}),
		require('@tailwindcss/forms'),
		require('@tailwindcss/typography')
	]
};

module.exports = config;
