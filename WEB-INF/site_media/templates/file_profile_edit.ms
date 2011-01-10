<div class="content-profile-edit">
    <h3>
      Edit File Profile&nbsp;&nbsp;-
      <span>
        <a class="done-editing" href="#">I'm done editing file profile.</a>
      </span>
    </h3>
    <div class="pub-profile-wrapper profile-wrapper">
    <h2>
        Public File Profile 
        <span>
        <img src="/site_media/images/add.png" />
        <a class="pub-add-field add-field" href="#">Add New Field</a>
        </span>
    </h2>
    <div id="pub-new-field" class="clear new-field">
        <div class="span-8">Field Name</div>
        <div class="span-10">Add Tags</div>
        <div class="span-8 clear">
        <input class="field-name" type="text" name="fieldname" />
        </div>
        <div class="span-10">
        <form class="add-tag-form" action="#/add_tag/public" method="post">
            <input type="text" name="tag" class="tag-input"/>
            <input type="submit" class="add-tag" value="Add" />
        </form>
        </div>
        <div class="clear">Currently Added Tags :</div>
        <div class="clear tag-field-data" style="margin-top:1em"></div>
        <div class="button-wrapper clear">
        <button class="create-action">create</button>
        <span>or <a class="form-cancel" href="#">I'm done adding fields</a></span>
        </div>
    </div>
    <div class="pub-fields-wrapper tag-fields-wrapper">
    {{#eXO::PublicProfile}}
      <div id="pub-tag-row-{{counter}}" class="tag-row clearfix">
        <div class="actions-wrapper">
          <div class="row-actions">
            <div class="buttons">
              <span class="button-action">
                <a class="delete close" id="pub-delete-action-{{counter}}" href="#">1</a>
              </span>
              <span class="button-action">
                <a class="edit" id="pub-edit-action-{{counter}}" href="#">Edit</a>
              </span>
            </div>
          </div>
        </div>
        <div class="tag-row-border-wrapper clearfix">
        <div class="tag-field-name span-3">{{eXO::FieldName}}</div>
        <div class="tag-field-data">
          {{#eXO::FieldData}}
            <span class="single-tag">
              {{eXO::FieldData}}
              <a class="close" href="#">1</a>
            </span>
          {{/eXO::FieldData}}
          {{#eXO::Terms}}
            <span class="single-tag">
            {{.}}
            <a class="close" href="#">1</a>
            </span>
          {{/eXO::Terms}}
        </div>
        </div>
      </div>
    {{/eXO::PublicProfile}}
    {{^eXO::PublicProfile}}
      <span class="quiet">There are no public tags yet. Why don't you add some now?</span>
    {{/eXO::PublicProfile}}
    </div>
    </div>
    <div class="clear pri-profile-wrapper profile-wrapper">
    <h2>
        Private File Profile (viewable only by friends)
        <span>
        <img src="/site_media/images/add.png" />
        <a class="pri-add-field add-field" href="#">Add New Field</a>
        </span>
    </h2>
    <div id="pri-new-field" class="clear new-field">
        <div class="span-8">Field Name</div>
        <div class="span-10">Add Tags</div>
        <div class="span-8 clear">
        <input class="field-name" type="text" name="fieldname" />
        </div>
        <div class="span-10">
        <form class="add-tag-form" action="#/add_tag/private" method="post">
            <input type="text" name="tag" class="tag-input"/>
            <input type="submit" class="add-tag" value="Add" />
        </form>
        </div>
        <div class="clear">Currently Added Tags :</div>
        <div class="clear tag-field-data" style="margin-top:1em"></div>
        <div class="button-wrapper clear">
        <button class="create-action">create</button>
        <span>or <a class="form-cancel" href="#">I'm done adding fields</a></span>
        </div>
    </div>
    <div class="pri-fields-wrapper tag-fields-wrapper">
    {{#eXO::PrivateProfile}}
      <div id="pri-tag-row-{{counter}}" class="tag-row clearfix">
        <div class="actions-wrapper">
          <div class="row-actions">
            <div class="buttons">
              <span class="button-action">
                <a class="delete close" id="pri-delete-action-{{counter}}" href="#">1</a>
              </span>
              <span class="button-action">
                <a class="edit" id="pri-edit-action-{{counter}}" href="#">Edit</a>
              </span>
            </div>
          </div>
        </div>
        <div class="tag-row-border-wrapper clearfix">
        <div class="tag-field-name span-3">{{eXO::FieldName}}</div>
        <div class="tag-field-data">
          {{#eXO::FieldData}}
            <span class="single-tag">
              {{eXO::FieldData}}
              <a class="close" href="#">1</a>
            </span>
          {{/eXO::FieldData}}
          {{#eXO::Terms}}
            <span class="single-tag">
            {{.}}
            <a class="close" href="#">1</a>
            </span>
          {{/eXO::Terms}}
        </div>
        </div>
      </div>
    {{/eXO::PrivateProfile}}
    {{^eXO::PrivateProfile}}
      <span class="quiet">There are no private tags yet. Why don't you add some now?</span>
    {{/eXO::PrivateProfile}}
    </div>
    </div>
</div>
