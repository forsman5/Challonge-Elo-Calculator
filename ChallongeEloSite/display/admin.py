from django.contrib import admin

from .models import *

# Register your models here.
admin.site.register(players)
admin.site.register(aliases)
admin.site.register(tournaments)
admin.site.register(matches)
admin.site.register(placings)
