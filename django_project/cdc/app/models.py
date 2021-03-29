from django.db import models
from django.db.models import DateTimeField, CharField, EmailField


class Author(models.Model):
    created_at: DateTimeField = models.DateTimeField(name='created_at', auto_now=True)
    description: CharField = models.CharField(max_length=400)
    email: EmailField = models.EmailField(name='email')
    name: CharField = models.CharField(max_length=100, name='name')
