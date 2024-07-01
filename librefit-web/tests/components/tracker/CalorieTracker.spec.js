import { cleanup, fireEvent, render, screen } from '@testing-library/svelte';
import { afterEach, beforeAll, describe, expect, it, vi } from 'vitest';
import CalorieTracker from '$lib/components/tracker/CalorieTracker.svelte';
import { tick } from 'svelte';

/**
 * @vitest-environment jsdom
 */
describe('CalorieTracker.svelte component', () => {});
