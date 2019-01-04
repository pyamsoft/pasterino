# pasterino

[![Get it on Google Play](https://raw.githubusercontent.com/pyamsoft/pasterino/master/art/google-play-badge.png)][1]

## Motivation

Some text input fields do not allow you to paste for some reason.

If you, like me, use a password manager with extremely long passwords, there  
is a high chance you do not actually know your password. Thus, when trying to  
enter this sensitive information into an input field, you rely on the Android  
copy-and-paste functionality. When paste is disabled, you are effectively  
unable to continue.

Pasterino uses an AccessibilityService to force a paste into the currently  
focused input field. It does not manage the clipboard itself, and so it does  
not have access to what information is copied into the clipboard. It is not  
a clipboard manager.

It is a simple application, built to solve an annoying problem:

I cannot paste my password into the Google Play dialog for Purchases.

Fear no more.

[1]: https://play.google.com/store/apps/details?id=com.pyamsoft.pasterino

## License

Apache 2

```
Copyright 2019 Peter Kenji Yamanaka

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
