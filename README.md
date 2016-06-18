# pasterino

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
