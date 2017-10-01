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

GPLv2

```
  The GPLv2 License

    Copyright (C) 2017  Peter Kenji Yamanaka

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
```
