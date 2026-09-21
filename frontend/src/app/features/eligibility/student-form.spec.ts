import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { distinctSubjectsValidator } from './student-form';

function buildRow(subjectName: string) {
  return new FormGroup({
    subjectName: new FormControl(subjectName),
    marks: new FormControl(80)
  });
}

describe('distinctSubjectsValidator', () => {
  it('passes when all subjects are distinct', () => {
    const array = new FormArray([buildRow('Physics'), buildRow('Chemistry'), buildRow('Mathematics')]);
    expect(distinctSubjectsValidator(array)).toBeNull();
  });

  it('flags a duplicate subject', () => {
    const array = new FormArray([buildRow('Physics'), buildRow('Physics'), buildRow('Mathematics')]);
    expect(distinctSubjectsValidator(array)).toEqual({ duplicateSubjects: true });
  });

  it('does not flag while rows are still unselected', () => {
    const array = new FormArray([buildRow(''), buildRow('')]);
    expect(distinctSubjectsValidator(array)).toBeNull();
  });
});
