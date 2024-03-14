const defaultProgressClass = 'bg-surface-900';
const successProgressClass = 'bg-success-500';
const errorProgressClass = 'bg-error-500';

const indicator = {
	/** css class for progress meter */
	meter: defaultProgressClass,
	/** css class for progress track */
	track: defaultProgressClass + '/30',
	/** progress indicator */
	progress: 0,
	/** assign to disabled property of component that started the interaction */
	actorDisabled: false,
	/**
	 * @return {indicator}
	 */
	start: function () {
		this.progress = undefined;
		this.actorDisabled = true;

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

		return this;
	},
	/**
	 * @return {indicator}
	 */
	reset: function () {
		this.progress = 0;
		this.meter = defaultProgressClass;

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
