// This is a replacement for the internal verifier in Firebase
// see: https://github.com/firebase/firebase-js-sdk/blob/cdada6c68f9740d13dd6674bcb658e28e68253b6/packages/auth/src/platform_browser/recaptcha/recaptcha_verifier.ts#L49-L279
export default class FirebaseRecaptchaVerifier {
    token;
    constructor(token) {
        this.token = token;
    }
    get type() {
        return 'recaptcha';
    }
    async verify() {
        return this.token;
    }
    // This property isn't in the original verifier type, but Firebase depends on this internally.
    // see: https://github.com/firebase/firebase-js-sdk/blob/dbd54f7c9ef0b5d78d491e26d816084a478bdf04/packages/auth/src/model/application_verifier.ts#L20-L25
    // see: https://github.com/firebase/firebase-js-sdk/blob/dbd54f7c9ef0b5d78d491e26d816084a478bdf04/packages/auth/src/platform_browser/strategies/phone.ts#L246
    // see: https://github.com/firebase/firebase-js-sdk/blob/cdada6c68f9740d13dd6674bcb658e28e68253b6/packages/auth/src/platform_browser/recaptcha/recaptcha_verifier.ts#L183-L188
    // see: https://github.com/expo/expo/issues/14780
    _reset() { }
}
//# sourceMappingURL=FirebaseRecaptchaVerifier.js.map