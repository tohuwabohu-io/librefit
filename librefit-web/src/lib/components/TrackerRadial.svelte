<script>
	export let limit = 2800,
		maximum = 3300,
		current = 1200;
	export let width = 512,
		height = 512;

	const stroke = 40;

	const outerRadius = (width - stroke) / 2;
	const innerRadius = outerRadius - stroke;

	$: outerEnd = (359 * current) / limit;
	$: innerEnd = (359 * current) / maximum;

	const polarToCartesian = (cx, cy, radius, angleDeg) => {
		const angleInRadians = ((angleDeg - 90) * Math.PI) / 180.0;

		return {
			x: cx + radius * Math.cos(angleInRadians),
			y: cy + radius * Math.sin(angleInRadians)
		};
	};

	const arc = (cx, cy, radius, startAngle, endAngle) => {
		const start = polarToCartesian(cx, cy, radius, endAngle);
		const end = polarToCartesian(cx, cy, radius, startAngle);

		const largeArcFlag = endAngle - startAngle <= 180 ? '0' : '1';

		return ['M', start.x, start.y, 'A', radius, radius, 0, largeArcFlag, 0, end.x, end.y].join(' ');
	};
</script>

<figure class="progress-radial relative overflow-hidden w-64 " data-testid="progress-radial">
	<svg viewBox="0 0 {width} {height}" class="rounded-full">
		<g stroke-width={stroke}>
			<path
				d={arc(width / 2, height / 2, outerRadius, 0, 359)}
				class="progress-radial-track fill-transparent stroke-primary-500/30"
				stroke-linecap="round"
			/>

			<path
				d={arc(width / 2, height / 2, outerRadius, 0, outerEnd)}
				class="progress-radial-track fill-transparent stroke-primary-500"
			/>

			{#if current > 0}
				<path
					d={arc(width / 2, height / 2, outerRadius, outerEnd, outerEnd)}
					class="progress-radial-track fill-transparent stroke-primary-500"
					stroke-linecap="round"
				/>
			{/if}
		</g>

		<g stroke-width={stroke}>
			<path
				d={arc(width / 2, height / 2, innerRadius, 0, 359)}
				class="progress-radial-track fill-transparent stroke-secondary-500/30"
				stroke-linecap="round"
			/>

			<path
				d={arc(width / 2, height / 2, innerRadius, 0, innerEnd)}
				class="progress-radial-track fill-transparent stroke-secondary-500"
			/>

			{#if current > 0}
				<path
					d={arc(width / 2, height / 2, innerRadius, innerEnd, innerEnd)}
					class="progress-radial-track fill-transparent stroke-secondary-500"
					stroke-linecap="round"
				/>
			{/if}
		</g>
		<text
			x={width / 2}
			y={height / 2}
			text-anchor="middle"
			dominant-baseline="middle"
			font-weight="bold"
			font-size="56"
			class="progress-radial-text fill-token"
		>
			{current} / {limit}
		</text>
		<text
			x={width / 2}
			y={height / 2 + 60}
			text-anchor="middle"
			dominant-baseline="middle"
			font-weight="bold"
			font-size="34"
			class="progress-radial-text fill-token"
		>
			{current} / {maximum}
		</text>
	</svg>
</figure>
