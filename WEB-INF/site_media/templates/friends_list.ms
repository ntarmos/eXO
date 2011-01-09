{{#eXO::Friends}}
  {{#first}}
  <div class="friends-row clear span-20 prepend-1 append-1">
  {{/first}}
    <div class="span-4 friend-card {{#last}}last{{/last}}">
      <img class="friend-thumb" height="100" width="100" alt="" src="/site_media/images/generic.jpg">
      <span class="friend-uid">{{UID}}</span>
      <div class="friend-name">
        <a href="#">{{ScreenName}}</a>
      </div>
    </div>
  {{#last}}
  </div>
  {{/last}}
{{/eXO::Friends}}
