package com.wisecal.android.ui.onboarding

import com.wisecal.android.model.PlannerMode
import org.junit.Assert.*
import org.junit.Test

class OnboardingStateTest {

    // ── 초기 상태 ──────────────────────────────────────────────────

    @Test fun `initial step is WELCOME`() {
        assertEquals(OnboardingStep.WELCOME, OnboardingState().currentStep)
    }

    @Test fun `initial calendarPermissionGranted is false`() {
        assertFalse(OnboardingState().calendarPermissionGranted)
    }

    // ── canProceed ─────────────────────────────────────────────────

    @Test fun `WELCOME canProceed false without planner mode`() {
        assertFalse(OnboardingState(currentStep = OnboardingStep.WELCOME, selectedPlannerMode = null).canProceed)
    }

    @Test fun `WELCOME canProceed true with planner mode`() {
        assertTrue(OnboardingState(currentStep = OnboardingStep.WELCOME, selectedPlannerMode = PlannerMode.DIARY).canProceed)
    }

    @Test fun `INPUT_MODE canProceed is true`() {
        assertTrue(OnboardingState(currentStep = OnboardingStep.INPUT_MODE).canProceed)
    }

    @Test fun `PERSONAL_EVENT canProceed is true`() {
        assertTrue(OnboardingState(currentStep = OnboardingStep.PERSONAL_EVENT).canProceed)
    }

    @Test fun `INTEGRATION canProceed is true`() {
        assertTrue(OnboardingState(currentStep = OnboardingStep.INTEGRATION).canProceed)
    }

    @Test fun `COMPLETE canProceed is true`() {
        assertTrue(OnboardingState(currentStep = OnboardingStep.COMPLETE).canProceed)
    }

    // ── nextStep ───────────────────────────────────────────────────

    @Test fun `nextStep WELCOME to INPUT_MODE`() {
        assertEquals(OnboardingStep.INPUT_MODE, OnboardingState(OnboardingStep.WELCOME).nextStep().currentStep)
    }

    @Test fun `nextStep INPUT_MODE to PERSONAL_EVENT`() {
        assertEquals(OnboardingStep.PERSONAL_EVENT, OnboardingState(OnboardingStep.INPUT_MODE).nextStep().currentStep)
    }

    @Test fun `nextStep PERSONAL_EVENT to INTEGRATION`() {
        assertEquals(OnboardingStep.INTEGRATION, OnboardingState(OnboardingStep.PERSONAL_EVENT).nextStep().currentStep)
    }

    @Test fun `nextStep INTEGRATION to COMPLETE`() {
        assertEquals(OnboardingStep.COMPLETE, OnboardingState(OnboardingStep.INTEGRATION).nextStep().currentStep)
    }

    @Test fun `nextStep COMPLETE stays COMPLETE`() {
        assertEquals(OnboardingStep.COMPLETE, OnboardingState(OnboardingStep.COMPLETE).nextStep().currentStep)
    }

    // ── isLastStep / isComplete ────────────────────────────────────

    @Test fun `isLastStep true only on INTEGRATION`() {
        assertTrue(OnboardingState(OnboardingStep.INTEGRATION).isLastStep)
        assertFalse(OnboardingState(OnboardingStep.WELCOME).isLastStep)
        assertFalse(OnboardingState(OnboardingStep.INPUT_MODE).isLastStep)
        assertFalse(OnboardingState(OnboardingStep.PERSONAL_EVENT).isLastStep)
    }

    @Test fun `isComplete true only on COMPLETE`() {
        assertTrue(OnboardingState(OnboardingStep.COMPLETE).isComplete)
        assertFalse(OnboardingState(OnboardingStep.INTEGRATION).isComplete)
    }
}
