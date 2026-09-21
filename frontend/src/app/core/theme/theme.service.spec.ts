import { TestBed } from '@angular/core/testing';
import { ThemeService } from './theme.service';

const STORAGE_KEY = 'course-eligibility-portal.theme';

describe('ThemeService', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
  });

  it('defaults to light when nothing is stored', () => {
    const service = TestBed.inject(ThemeService);
    expect(service.currentMode()).toBe('light');
    expect(service.isDark()).toBe(false);
  });

  it('restores a previously saved preference', () => {
    localStorage.setItem(STORAGE_KEY, 'dark');
    const service = TestBed.inject(ThemeService);
    expect(service.currentMode()).toBe('dark');
    expect(service.isDark()).toBe(true);
  });

  it('toggle flips the mode and persists it', () => {
    const service = TestBed.inject(ThemeService);
    service.toggle();
    expect(service.currentMode()).toBe('dark');
    expect(localStorage.getItem(STORAGE_KEY)).toBe('dark');

    service.toggle();
    expect(service.currentMode()).toBe('light');
    expect(localStorage.getItem(STORAGE_KEY)).toBe('light');
  });
});
