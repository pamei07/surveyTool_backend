import {Component, Input, OnInit} from '@angular/core';
import {Survey} from "../../model/survey";
import {Checkbox} from "../../model/checkbox";

@Component({
  selector: 'checkbox-list',
  templateUrl: 'checkbox-list.component.html'
})

export class CheckboxListComponent implements OnInit {

  checkbox: Checkbox;
  @Input() indexQuestion!: number;
  @Input() indexQuestionGroup!: number;
  @Input() survey!: Survey;

  constructor() {
    this.checkbox = new Checkbox();
  }

  ngOnInit() {
  }

  onSubmit() {
    console.log(this.checkbox);

    this.survey
      .questionGroups![this.indexQuestionGroup]
      .questions![this.indexQuestion]
      .checkboxGroup!
      .checkboxes!.push(this.checkbox);

    sessionStorage.setItem('newSurvey', JSON.stringify(this.survey));

    this.checkbox = new Checkbox();
  }
}
