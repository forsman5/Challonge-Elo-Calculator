from django.urls import path

from . import views

urlpatterns = [
    #ex /display/
    path('', views.index, name='index'),
]
