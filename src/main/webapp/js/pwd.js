var pwApp;

var CryptoJSAesJson = {
    stringify: function(cipherParams) {
        var j = { ct: cipherParams.ciphertext.toString(CryptoJS.enc.Base64) };
        if (cipherParams.iv) j.iv = cipherParams.iv.toString();
        if (cipherParams.salt) j.s = cipherParams.salt.toString();
        return JSON.stringify(j);
    }
};

var cookieUtil = {
    deleteCookie: function(name) {
        var host = location.hostname;
        var domain = "";

        if (host.indexOf('.') !== -1) {
            var parts = host.split('.');
            if (parts.length >= 2) {
                domain = "." + parts[parts.length - 2] + "." + parts[parts.length - 1];
            }
        }

        var expireDate = "Thu, 01 Jan 1970 00:00:00 UTC";

        document.cookie = name + "=; expires=" + expireDate + "; path=/;";
        if (domain) {
            document.cookie = name + "=; expires=" + expireDate + "; path=/; domain=" + domain + ";";
        }
    }
};

$(document).ready(function() {
    pwApp = new Vue({
        el: "#pwFormSection",
        data() {
            return {
                isSSO: typeof isSSO !== 'undefined' ? isSSO : false,
                empNo: '',
                oldPw: '',
                newPw: '',
                confirmPw: '',
                empNoState: null,
                oldPwState: null,
                inputNewPwdState: null,
                confirmPwdState: null,
                resetLabel: '초기화',
                changeBtnLabel: '비밀번호 변경',
                pwPolicy: {
                    length: false,
                    combination: false,
                    noRepeat: false,
                    noSequence: false,
                    notId: false
                },
                refData: '',
                isLoading: false
            };
        },
        mounted() {
            var vm = this;

            if (vm.isSSO && typeof ssoUserId !== 'undefined' && ssoUserId !== '' && ssoUserId !== 'null') {
                vm.empNo = ssoUserId;
                vm.inputEmpNoCheck();
            }
        },
        computed: {
            isPwPolicyValid() {
                return Object.values(this.pwPolicy).every(v => v === true);
            },
            pwdChangeBtnDisabled() {
                var isResetPage = document.title.indexOf('초기화') !== -1;

                var isValid = (
                    this.empNo.trim().length > 0 &&
                    (isResetPage ? true : this.oldPw.length > 0) &&
                    this.newPw.length > 0 &&
                    this.confirmPw.length > 0 &&
                    (this.newPw === this.confirmPw) &&
                    this.isPwPolicyValid
                );
                return !isValid;
            }
        },
        methods: {
            inputEmpNoCheck() {
                this.empNoState = this.empNo.trim().length > 0;
                if (this.newPw) this.validatePwPolicy();
            },
            inputOldPwdCheck() {
                this.oldPwState = this.oldPw.length > 0;
            },
            inputNewPwdCheck() {
                this.validatePwPolicy();
                this.inputNewPwdState = this.isPwPolicyValid;
                this.inputConfirmPwdCheck();
            },
            validatePwPolicy() {
                const pw = this.newPw;
                const id = this.empNo.trim();

                this.pwPolicy.length = pw.length >= 8 && pw.length <= 16;

                const hasLetter = /[a-zA-Z]/.test(pw);
                const hasNum = /[0-9]/.test(pw);
                const hasSpec = /[!@#$%^&*()_+|<>?:{}]/.test(pw);
                this.pwPolicy.combination = (hasLetter && hasNum && hasSpec);

                this.pwPolicy.noRepeat = !/(\w)\1\1/.test(pw);

                let isSeq = false;
                for (let i = 0; i < pw.length - 2; i++) {
                    const p1 = pw.charCodeAt(i);
                    const p2 = pw.charCodeAt(i + 1);
                    const p3 = pw.charCodeAt(i + 2);
                    if ((p1 + 1 === p2 && p2 + 1 === p3) || (p1 - 1 === p2 && p2 - 1 === p3)) {
                        isSeq = true;
                        break;
                    }
                }
                this.pwPolicy.noSequence = !isSeq;

                this.pwPolicy.notId = id.length > 0 ? !pw.includes(id) : true;
            },
            inputConfirmPwdCheck() {
                if (!this.confirmPw) {
                    this.confirmPwdState = null;
                } else {
                    this.confirmPwdState = (this.newPw === this.confirmPw);
                }
            },
            showAlert(msg, icon = 'warning', title = '알림', callback) {
                Swal.fire({
                    title: title,
                    text: msg,
                    icon: icon,
                    confirmButtonColor: '#0096FF',
                    confirmButtonText: '확인'
                }).then(() => {
                    if (callback) callback();
                });
            },
            showLoading() {
                Swal.fire({
                    title: '처리 중입니다',
                    text: '잠시만 기다려 주세요...',
                    allowOutsideClick: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });
            },
            onEnterSubmit() {
                if (this.pwdChangeBtnDisabled || this.isLoading) return;

                const actionMap = {
                    'expPwdReset.jsp': 'expPwReset',
                    'myPwdReset.jsp': 'pwReset',
                    'myPwdChange.jsp': 'pwChange'
                };

                const fileName = window.location.pathname.split('/').pop();
                const methodName = actionMap[fileName];

                if (this[methodName]) {
                    this[methodName]();
                }
            },
            pwChange() {
                var vm = this;
                if (!vm.pwdChangeBtnDisabled && !vm.isLoading) {
                    vm.isLoading = true;
                    vm.showLoading();

                    axios({
                        method: 'POST',
                        /*url: 'https://rms.webseal.iamapp.com/MyPwdServlet.do',*/
						url: 'https://rms.iamapp.com:9446/MyPwdServlet.do',
						headers: {
					        'Content-Type': 'application/json'
					    },
                        data: {
                            EXE: 'changePwd',
                            USER_ID: vm.empNo,
                            PWD: CryptoJS.AES.encrypt(vm.newPw, 'precursorEncryptKey', { format: CryptoJSAesJson }).toString(),
                            OLD_PWD: CryptoJS.AES.encrypt(vm.oldPw, 'precursorEncryptKey', { format: CryptoJSAesJson }).toString()
                        }
                    }).then(function(response) {
                        Swal.close();
                        if (response.data.result) {
                            vm.showAlert(response.data.msg, 'success', '완료', function() {
                                window.open('about:blank', '_self');
                                window.close();
                            });
                        } else {
                            vm.showAlert(response.data.msg);
                        }
                    }).catch(function(error) {
                        Swal.close();
                        vm.showAlert('시스템 통신 오류!', 'error', '경고');
                    }).finally(function() {
                        vm.isLoading = false;
                    });
                }
            },
            pwReset() {
                var vm = this;
                if (!vm.pwdChangeBtnDisabled && !vm.isLoading) {
                    vm.isLoading = true;
                    vm.showLoading();

                    axios({
                        method: 'POST',
                        /*url: 'https://rms.webseal.iamapp.com/MyPwdServlet.do',*/
						url: 'https://rms.iamapp.com:9446/MyPwdServlet.do',
						headers: {
					        'Content-Type': 'application/json'
					    },
                        data: {
                            EXE: 'resetPwd',
                            USER_ID: vm.empNo,
                            PWD: CryptoJS.AES.encrypt(vm.newPw, 'precursorEncryptKey', { format: CryptoJSAesJson }).toString()
                        }
                    }).then(function(response) {
                        Swal.close();
                        if (response.data.result) {
                            vm.showAlert(response.data.msg, 'success', '완료', function() {
                                window.open('about:blank', '_self');
                                window.close();
                            });
                        } else {
                            vm.showAlert(response.data.msg);
                        }
                    }).catch(function(error) {
                        Swal.close();
                        vm.showAlert('시스템 통신 오류!', 'error', '경고');
                    }).finally(function() {
                        vm.isLoading = false;
                    });
                }
            },
            expPwReset() {
                var vm = this;
                if (!vm.pwdChangeBtnDisabled && !vm.isLoading) {
                    vm.isLoading = true;
                    vm.showLoading();

                    axios({
                        method: 'POST',
                        /*url: 'https://rms.webseal.iamapp.com/MyPwdServlet.do',*/
						url: 'https://rms.iamapp.com:9446/MyPwdServlet.do',
						headers: {
					        'Content-Type': 'application/json'
					    },
                        data: {
                            EXE: 'resetPwd',
                            USER_ID: vm.empNo,
                            PWD: CryptoJS.AES.encrypt(vm.newPw, 'precursorEncryptKey', { format: CryptoJSAesJson }).toString()
                        }
                    }).then(function(response) {
                        Swal.close();
                        if (response.data.result) {
                            vm.showAlert(response.data.msg, 'success', '완료', function() {
                                cookieUtil.deleteCookie("PD-H-SESSION-ID");
                                window.location.replace(vm.refData);
                            });
                        } else {
                            vm.showAlert(response.data.msg);
                        }
                    }).catch(function(error) {
                        Swal.close();
                        vm.showAlert('시스템 통신 오류!', 'error', '경고');
                    }).finally(function() {
                        vm.isLoading = false;
                    });
                }
            }
        }
    });
});