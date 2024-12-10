# librefit

![GitHub Release](https://img.shields.io/github/v/release/thwbh/librefit) ![Coverage](https://raw.githubusercontent.com/thwbh/librefit/badges/jacoco.svg)

LibreFit is my hobby project that allows you to calculate your TDEE (Total Daily Energy Expenditure) based on your body
metrics, create goals for weight loss or weight gain from that and encourages you to track your daily calorie intake.
Based in your input, you will see easy to understand charts to keep on track with your goal - all that with privacy
first in mind.

* [Issue Tracker](https://github.com/thwbh/librefit/issues)
* [Discussions](https://github.com/thwbh/librefit/discussions)

## Introduction

LibreFit was created after tracking my caloric intake with some Excel and Google Sheets over a year. Out of boredom I
decided to try a new stack I've never done anything productive with, I quite enjoyed the journey and thus this project
was born.

### Dependencies

- rust 1.77.2
- diesel 2.2.6 
- tauro 2.0
- sqlite 3
- sveltekit 2.5.2
- skeleton 2.9.0 

## Structure

The project follows the default [tauri setup](https://tauri.app/start/create-project/).

```
librefit
|- librefit-web
|- src-tauri
```

## setup

Navigate to src-tauri/ and issue

```shell
cargo build
```

## start

To start the development server, go to the project root and issue

```shell
cargo tauri dev
```

## Attribution
The avatars were created with hotpot: https://hotpot.ai/art-generator

## Contribute

If you have any suggestions, impediments or things that absolutely annoy you, please refer to the issue tracker linked
at the top of this document. When you are interested in participating in development, feel free to contact me.

