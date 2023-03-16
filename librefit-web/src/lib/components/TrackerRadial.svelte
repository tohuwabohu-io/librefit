<script>
    import {ProgressRadial} from "@skeletonlabs/skeleton";
</script>


<figure class="progress-radial relative overflow-hidden w-64 " data-testid="progress-radial"
        role="meter" aria-labelledby="" aria-valuenow="60" aria-valuetext="60%" aria-valuemin="0" aria-valuemax="100">
    <svg viewBox="0 0 512 512" class="rounded-full">
        <circle cx="50%" cy="50%" r="166" stroke-width="60"
                class="progress-radial-track fill-transparent stroke-secondary-500/30"></circle>
        <circle cx="50%" cy="50%" style="stroke-dasharray: 804.25px, 804.25px; stroke-dashoffset: 216.2px;" r="166"
                stroke-width="60"
                class="progress-radial-meter fill-transparent transition-[stroke-dashoffset] duration-200 -rotate-90 origin-[50%_50%] stroke-secondary-500"></circle>
        <circle class="progress-radial-track fill-transparent stroke-primary-500/30" stroke-width="120" r="256" cx="50%"
                cy="50%"></circle>
        <circle class="progress-radial-meter fill-transparent transition-[stroke-dashoffset] duration-200 -rotate-90 origin-[50%_50%] stroke-primary-500"
                stroke-width="120" r="256" cx="50%" cy="50%"
                style="stroke-dasharray: 1608.5px, 1608.5px; stroke-dashoffset: 643.398px;"></circle>
        <text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" font-weight="bold" font-size="56" class="progress-radial-text fill-token">60%</text>
    </svg>
</figure>