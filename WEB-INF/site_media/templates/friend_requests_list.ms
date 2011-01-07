{{#eXO::Friend_Requests}}
  <div id="friend-request-row-{{index}}" class="friend-request-row clear span-12 prepend-1 append-1">
    <div class="span-7" >
      <img class="friend-request-thumb" height="50" width="50" alt="Become friend with {{ScreenName}}" src="/site_media/images/generic.jpg">
      <div class="friend-name">
        <a href="#">{{ScreenName}}</a>
      </div>
    </div>
    <div class="row-actions span-5 last">
      <span class="button-action">
        <form action="#/friend_requests/accept/" method="post">
          <input type="submit" value="Accept"/>
          <input type="hidden" name="accept_uid" value="{{UID}}"/>
          <input type="hidden" name="elem_id" value="friend-request-row-{{index}}"/>
        </form>
      </span>
      <span class="button-action">
        <form action="#/friend_requests/reject/" method="post">
          <input type="submit" value="Reject"/>
          <input type="hidden" name="reject_uid" value="{{UID}}"/>
          <input type="hidden" name="elem_id" value="friend-request-row-{{index}}"/>
        </form>
      </span>
    </div>
  </div>
{{/eXO::Friend_Requests}}
