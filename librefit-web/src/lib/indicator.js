const lightProgressClass = 'bg-surface-900';
const darkProgressClass = 'bg-surface-100';
const successProgressClass = 'bg-success-500';
const errorProgressClass = 'bg-error-500';

let theme;

const indicator = {
	/** css class for progress meter */
	meter: theme === 'dark' ? darkProgressClass : lightProgressClass,
	/** css class for progress track */
	track: theme === 'dark' ? darkProgressClass : lightProgressClass + '/30',
	/** progress indicator */
	progress: 0,
	/** assign to disabled property of component that started the interaction */
	actorDisabled: false,
	/** set in start on demand, will be reset after finish */
	actor: undefined,
	invisible: '',
	/**
	 * @param [actor]
	 * @return {indicator}
	 */
	start: function (actor) {
		this.progress = undefined;
		this.actorDisabled = true;
		this.invisible = '';

		if (actor) {
			actor.disabled = true;
			this.actor = actor;
		}

		return this;
	},
	/**
	 * @return {indicator}
	 */
	finishSuccess: function () {
		this.meter = successProgressClass;
		return this.finish();
	},
	/**
	 * @return {indicator}
	 */
	finishError: function () {
		this.meter = errorProgressClass;
		return this.finish();
	},
	/**
	 * @return {indicator}
	 */
	finish: function () {
		this.track = this.meter + '/30';
		this.progress = 100;
		this.actorDisabled = false;

		if (this.actor) {
			this.actor.disabled = false;
			this.actor = undefined;
		}

		return this.hide();
	},
	hide: function () {
		this.invisible = 'invisible';

		return this;
	},
	/**
	 * @return {indicator}
	 */
	reset: function () {
		this.progress = 0;
		this.meter = theme === 'dark' ? darkProgressClass : lightProgressClass;

		if (this.actor) {
			this.actor.disabled = false;
			this.actor = undefined;
		}

		return this;
	},
	toggle: function (toggle) {
		theme = toggle;

		if ('dark' === toggle) {
			this.meter = darkProgressClass;
			this.track = darkProgressClass + '/30';
		} else {
			this.meter = lightProgressClass;
			this.track = lightProgressClass + '/30';
		}

		return this;
	}
};

Object.assign(Indicator.prototype, indicator);

/**
 * Holds values for the Skeleton Progress component.
 * Use each method to re-assign constructed object or otherwise
 * values won't be updated in the markup.
 *
 * @constructor
 */
export function Indicator() {}
