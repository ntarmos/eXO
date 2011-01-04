{{#eXO::ScoreBoard}}
  <div class="entry">
    <span class="row-number">{{index}}.</span>
    {{#eXO::Entry}}
      {{#eXO::CCE}}
        <span class="uid">{{eXO::UID}}</span>
        {{#eXO::Profiles}}
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
        {{/eXO::Profiles}}
      {{/eXO::CCE}}
    {{/eXO::Entry}}
  </div>
{{/eXO::ScoreBoard}}
