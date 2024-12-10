import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';
import svg from '@poppanator/sveltekit-svg';
import purgeCss from 'vite-plugin-tailwind-purgecss';

export default defineConfig({
	plugins: [
		sveltekit(),
		purgeCss(),
		svg({
			includePaths: ['./src/lib/assets/icons/', './src/lib/assets/images/'],
			svgoOptions: {
				multipass: true,
				plugins: [
					{
						name: 'preset-default',
						// by default svgo removes the viewBox which prevents svg icons from scaling
						// not a good idea! https://github.com/svg/svgo/pull/1461
						params: { overrides: { removeViewBox: false } }
					}
				]
			}
		})
	],
	test: {
		setupFiles: ['./tests/__mocks__/skeletonProxy.ts']
	}
});
