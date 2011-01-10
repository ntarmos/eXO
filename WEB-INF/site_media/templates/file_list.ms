{{#profiles}}
<div id="file-row-{{counter}}" class="file-row {{zebra}} clear">
  <div class="file-data">
    <span class="filename">{{eXO::Filename}}</span>
    
    <div class="actions-wrapper">
    <div class="row-actions">
      <div class="buttons">
        <span class="button-action">
          <a class="show" id="file-show-action-{{counter}}" href="#">Show</a>
        </span>
        <span class="button-action">
          <a class="edit" id="file-edit-action-{{counter}}" href="#">Edit</a>
        </span>
        <span class="button-action">
          <a class="delete close" id="file-delete-action-{{counter}}" href="#">1</a>
        </span>
      </div>
    </div>
    </div>
    
    <span id="{{file_id}}" class="file-id append-2">{{file_id}}</span>
  </div>
  <div class="tags-wrapper clear">
  {{#eXO::Profile}}
    <div class="tag-row">
      <div class="tag-field-name">{{eXO::FieldName}}:</div>
      <div class="tag-field-data quiet">
        {{#eXO::FieldData}}
          <span class="single-tag">
            {{eXO::FieldData}}
          </span>
        {{/eXO::FieldData}}
        {{#eXO::Terms}}
          <span class="single-tag">
          {{.}}
          </span>
        {{/eXO::Terms}}
      </div>
    </div>
  {{/eXO::Profile}}
  </div>
</div>
{{/profiles}}
{{^profiles}}
<span class="quiet">There are no shared files yet. Why don't you share some now?</span>
{{/profiles}}
