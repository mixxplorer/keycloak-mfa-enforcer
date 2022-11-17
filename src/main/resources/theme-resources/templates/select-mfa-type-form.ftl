<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "title">
        ${msg("mfaEnforcerFormTitle")}
    <#elseif section = "header">
        ${msg("mfaEnforcerFormTitle")}
    <#elseif section = "form">

        <h1>${msg("mfaEnforcerHello")} ${username}</h1>
        <p>
            ${msg("mfaEnforcerExplanation")}
        </p>
        <ul>
            <#if enabledOptions?seq_contains("webauthn")>
            <li><b>${msg("mfaEnforcerWebAuthnOptionTitle")}:</b> ${msg("mfaEnforcerWebAuthnOptionDescription")}</li>
            </#if>
            <#if enabledOptions?seq_contains("totp")>
            <li><b>${msg("mfaEnforcerTOTPOptionTitle")}:</b> ${msg("mfaEnforcerTOTPOptionDescription")}</li>
            </#if>
            <#if enabledOptions?size == 0>
            <b>${msg("mfaEnforcerNoOptions")}</b>
            </#if>
        </ul>
        <form id="kc-select-mfa-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <#if enabledOptions?seq_contains("webauthn")>
                    <input
                        class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                        type="submit"
                        name="webauthn"
                        value="${msg("mfaEnforcerWebAuthnOptionButton")}"
                    />
                    </#if>
                    <#if enabledOptions?seq_contains("totp")>
                    <input
                        class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                        type="submit"
                        name="totp"
                        value="${msg("mfaEnforcerTOTPOptionButton")}"
                    />
                    </#if>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
